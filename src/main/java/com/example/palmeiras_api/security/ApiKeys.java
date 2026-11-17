package com.example.palmeiras_api.security;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.security.*;
import java.nio.charset.StandardCharsets;
@Service public class ApiKeys {
 public record KeyInfo(String id,String usuario) {}
 private final Map<String,KeyInfo> keys=new ConcurrentHashMap<>();
 public static String hash(String s){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
 public String create(String user){String raw=UUID.randomUUID().toString()+UUID.randomUUID(); keys.put(hash(raw),new KeyInfo(UUID.randomUUID().toString(),user));return raw;}
 public KeyInfo find(String raw){return raw==null?null:keys.get(hash(raw));}
 public List<KeyInfo> list(){return keys.values().stream().sorted(Comparator.comparing(KeyInfo::id)).toList();}
 public boolean revoke(String id){return keys.entrySet().removeIf(e->e.getValue().id().equals(id));}
}
