package org.example.route;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.json.simple.JsonObject;
import org.example.process.Inspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProxyRoute extends RouteBuilder {
    //정규식, 마스킹
    private final static String last_6_char_pattern = "(.{6}$)";

    public static String maskSSN(String ssn) {
        if (ssn == null || "".equals(ssn) || ssn.length() < 6) return ssn;
        return ssn.replaceAll(last_6_char_pattern, "******");
    }

    String str = null;


    final
    private Inspector inspector;

    public ProxyRoute(Inspector inspector) {
        this.inspector = inspector;
    }

    @Override
    public void configure() throws Exception {


//      from("jetty:https://0.0.0.0:9443?matchOnUriPrefix=true")
//        .routeId("httpsProxyRoute")
//        .setHeader(Exchange.HTTP_SCHEME, constant("https"))
////        .setHeader(Exchange.HTTP_HOST,constant("dblee"))
////        .setHeader(Exchange.HTTP_PORT,constant("9443"))
////        .setBody(constant("https-body-test"))
//        .to("direct:proxy");

        from("jetty:http://0.0.0.0:9090?matchOnUriPrefix=true")
                .routeId("httpProxyRoute")
                .setHeader(Exchange.HTTP_SCHEME, constant("http"))
                .setHeader(Exchange.HTTP_HOST, constant("0.0.0.0"))
                .setHeader(Exchange.HTTP_PATH, constant("/"))
                .setHeader(Exchange.HTTP_PORT,constant("9090"))

                .setBody(constant("http-body-test"))
                .process(new Processor() {
                    @Override
                    //processor 에서 변경 가능
                    public void process(Exchange exchange) throws Exception {

                        JSONParser jsonParser = new JSONParser();
                        String temp = exchange.getIn().getBody(String.class);
                      //  Object jsonObj = JsonPath.parse(temp).read("$[?(@.ssn)]");

                        JSONObject jsonObj = (JSONObject) jsonParser.parse(temp);
                        if (jsonObj.containsKey("ssn")) {
                            str = maskSSN(jsonObj.get("ssn").toString());
                        }
                       jsonObj.replace("ssn", jsonObj.get("ssn"), str);
                        exchange.getOut().setBody(jsonObj);
                    }
                })
//
//                .setProperty("ssn").jsonpath("$.ssn")
//                .log("ssn: ${property.ssn}")
                .to("http://localhost:8080/camel/greetings/Jacopo");
                //.toD("http://fuse-demo-app-n-fuse.apps.cluster-e27c.e27c.sandbox1520.opentlc.com/camel/greetings/Jacopo");
        from("direct:proxy")
                .log(">>>>>>> ${date:now}")
                //.to("log:info?showHeaders=true")
                .log("CamelHttpScheme: ${headers.CamelHttpScheme}")
                .log("CamelHttpUrl:    ${headers.CamelHttpUrl}")
                .log("CamelHttpHost:   ${headers.Host}")
                .log("CamelHttpHost:   ${headers.CamelHttpHost}")
                .log("CamelHttpPort:   ${headers.CamelHttpPort}")
                .log("CamelHttpPath:   ${headers.CamelHttpPath}")

                .log("${headers.CamelHttpScheme}://${headers.Host}/${headers.CamelHttpPath}?bridgeEndpoint=true")
                .log("${body}")
             //   .toD("jetty:${headers.CamelHttpScheme}://${headers.Host}/${headers.CamelHttpPath}?bridgeEndpoint=true")
                .log("proxy response : \n${body}");

    }
}
