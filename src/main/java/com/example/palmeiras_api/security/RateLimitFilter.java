package com.example.palmeiras_api.security;
import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException; import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component; import org.springframework.core.annotation.Order; import org.springframework.beans.factory.annotation.Value; import org.springframework.web.filter.OncePerRequestFilter;
@Component @Order(20) public class RateLimitFilter extends OncePerRequestFilter {
 private record Window(long start,int used){} private final ConcurrentHashMap<String,Window> windows=new ConcurrentHashMap<>(); private final int limit;
 public RateLimitFilter(@Value("${app.rate-limit:60}") int limit){this.limit=limit;}
 protected boolean shouldNotFilter(HttpServletRequest r){return r.getMethod().equals("OPTIONS")||!r.getServletPath().matches("/(jogadores|equipes|estadios|campeonatos|contratos|salarios|api-keys)(/.*)?");}
 protected void doFilterInternal(HttpServletRequest r,HttpServletResponse s,FilterChain c)throws ServletException,IOException{
 long now=System.currentTimeMillis(); String client=r.getAttribute("apiClient")!=null?r.getAttribute("apiClient").toString():r.getRemoteAddr();
 windows.entrySet().removeIf(e->now-e.getValue().start()>120000);
 Window w=windows.compute(client,(k,v)->v==null||now-v.start()>=60000?new Window(now,1):new Window(v.start(),Math.min(v.used()+1,limit+1)));
 s.setHeader("X-Rate-Limit-Limit",String.valueOf(limit));s.setHeader("X-Rate-Limit-Remaining",String.valueOf(Math.max(0,limit-w.used())));
 if(w.used()>limit){s.setHeader("Retry-After",String.valueOf(Math.max(1,(60000-now+w.start()+999)/1000)));ApiKeyFilter.error(s,429,"Limite de requisicoes excedido");return;}c.doFilter(r,s);
 }
}
