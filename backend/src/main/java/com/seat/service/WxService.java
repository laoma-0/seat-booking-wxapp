package com.seat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WxService {

    @Value("${wx.appid}")
    private String appid;

    @Value("${wx.secret}")
    private String secret;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    public String code2Session(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid
                + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
        String json = rest.getForObject(url, String.class);
        try {
            JsonNode node = om.readTree(json);
            if (node.has("errcode")) {
                throw new RuntimeException("code2Session 失败, errcode=" + node.get("errcode").asText()
                        + ", errmsg=" + node.get("errmsg").asText());
            }
            return node.get("openid").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("code2Session 解析失败: " + json, e);
        }
    }
}
