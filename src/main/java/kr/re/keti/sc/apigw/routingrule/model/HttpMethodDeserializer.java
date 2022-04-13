package kr.re.keti.sc.apigw.routingrule.model;

import java.io.IOException;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class HttpMethodDeserializer extends StdDeserializer<HttpMethod> { 

	private static final long serialVersionUID = 1L;

	public HttpMethodDeserializer() { 
        this(null); 
    } 
 
    public HttpMethodDeserializer(Class<?> vc) { 
        super(vc); 
    }
    
    @Override
    public HttpMethod deserialize(JsonParser jp, DeserializationContext ctxt) 
      throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        
        if (node != null && node.asText() != null) {
        	if (HttpMethod.resolve(node.asText()) != null) {
        		return HttpMethod.resolve(node.asText());
        	}
        	throw new IllegalArgumentException(String.format("No matching constant for [%s]", node.asText()));
        }
        return null;
    }
}
