package com.example.palmeiras_api.security;
import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException;
import org.springframework.stereotype.Component; import org.springframework.core.annotation.Order; import org.springframework.beans.factory.annotation.Value; import org.springframework.web.filter.OncePerRequestFilter;
@Component @Order(10) public class ApiKeyFilter extends OncePerRequestFilter {
 private final ApiKeys keys; private final String admin;
 public ApiKeyFilter(ApiKeys keys,@Value("${app.admin-key:}") String admin){this.keys=keys;this.admin=admin;}
 protected void doFilterInternal(HttpServletRequest r,HttpServletResponse s,FilterChain c)throws ServletException,IOException{
 String path=r.getServletPath(), method=r.getMethod(), raw=r.getHeader("X-API-Key");
 boolean management=path.equals("/api-keys")||path.startsWith("/api-keys/");
 boolean business=path.matches("/(jogadores|equipes|estadios|campeonatos|contratos|salarios)(/.*)?");
 boolean readOnly = method.equals("GET") || method.equals("HEAD");
 boolean sensitive = path.matches("/(contratos|salarios)(/.*)?");
 if (!method.equals("OPTIONS") && (management || (business && (!readOnly || sensitive)))) {
 boolean isAdmin=!admin.isBlank() && raw!=null && java.security.MessageDigest.isEqual(admin.getBytes(java.nio.charset.StandardCharsets.UTF_8),raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
 var key=keys.find(raw);
 if(!isAdmin && key==null){error(s,401,"Chave de API ausente ou invalida");return;}
 if(management&&!isAdmin){error(s,403,"Acesso administrativo necessario");return;}
 r.setAttribute("apiClient",isAdmin?"admin":key.id());
 } c.doFilter(r,s); }
 static void error(HttpServletResponse s,int status,String msg)throws IOException{s.setStatus(status);s.setContentType("application/json");s.getWriter().write("{\"status\":"+status+",\"message\":\""+msg+"\"}");}
}
