# Seguridad — JWT + Spring Security

Paquete: `co.edu.unbosque.horarios.security`.

## Reglas (RNF-04)

| Ruta | Acceso |
|------|--------|
| `/api/publico/**` | Sin autenticación → 200 |
| `/api/auth/login` | Sin autenticación → 200 |
| `/ws/**` (WebSocket) | Sin autenticación (validación en handler) |
| `/api/docentes/{id}/disponibilidad` | ADMIN o DOCENTE (propio) |
| `/api/**` (resto) | ADMIN |
| Sin token / token inválido | → 401 |
| Token válido, rol insuficiente | → 403 |

## SecurityConfig

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Público
                .requestMatchers("/api/publico/**").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/ws/**").permitAll()
                // Docente puede registrar su propia disponibilidad
                .requestMatchers(HttpMethod.POST, "/api/docentes/*/disponibilidad")
                    .hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.GET, "/api/docentes/*/disponibilidad")
                    .hasAnyRole("ADMIN", "DOCENTE")
                // Resto requiere ADMIN
                .requestMatchers("/api/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> res.setStatus(401))
                .accessDeniedHandler((req, res, e) -> res.setStatus(403))
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder pe) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(pe)
            .and().build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

## JwtUtil

```java
@Component
public class JwtUtil {
    @Value("${app.jwt.secret}") private String secret;
    @Value("${app.jwt.expiration}") private long expirationMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generar(Usuario usuario) {
        return Jwts.builder()
            .subject(usuario.getUsername())
            .claim("rol", usuario.getRol())
            .claim("usuarioId", usuario.getUsuarioId())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(key())
            .compact();
    }

    public Claims validar(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
    }

    public String extraerUsername(String token) {
        return validar(token).getSubject();
    }
}
```

## JwtAuthFilter

```java
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String username = jwtUtil.extraerUsername(token);
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException e) {
                // Token inválido — Spring Security devolverá 401 automáticamente
            }
        }
        chain.doFilter(req, res);
    }
}
```

## UserDetailsServiceImpl

```java
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return User.builder()
            .username(usuario.getUsername())
            .password(usuario.getPassword())
            .roles(usuario.getRol())  // Spring Security añade automáticamente "ROLE_" prefix
            .disabled(!usuario.getActivo())
            .build();
    }
}
```

## AuthService

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final UsuarioRepository usuarioRepo;
    private final JwtUtil jwtUtil;

    public LoginResponse login(String username, String password) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        Usuario usuario = usuarioRepo.findByUsername(username).orElseThrow();
        String token = jwtUtil.generar(usuario);
        return new LoginResponse(token, usuario.getRol(), usuario.getUsername());
    }

    public UsuarioDTO usuarioActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioRepo.findByUsername(username).orElseThrow();
        return UsuarioDTO.from(u);
    }
}
```

## BadCredentials → 401

Spring Security devuelve 401 automáticamente para `BadCredentialsException` cuando se lanza desde el filtro o desde el `AuthenticationManager`. En el handler personalizado:

```java
@ExceptionHandler(BadCredentialsException.class)
public ResponseEntity<ErrorDTO> handleBadCreds(BadCredentialsException e) {
    return ResponseEntity.status(401).body(new ErrorDTO("CREDENCIALES_INVALIDAS", e.getMessage(), null));
}
```

## Configuración de Secret

En `application.yml`:

```yaml
app:
  jwt:
    secret: ${JWT_SECRET:cambiar-en-produccion-clave-de-al-menos-32-caracteres}
    expiration: 86400000  # 24h
```

**Nunca** dejar `JWT_SECRET` vacío en producción. Configurar como variable de entorno.
