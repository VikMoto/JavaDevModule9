package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;
    private static final String LAST_TIMEZONE = "lastTimezone";
    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("E:\\Business\\Code\\Projects\\JavaDev\\JavaDevModule9\\src\\main\\webapp\\WEB-INF\\templates\\");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        setSession(req);

        Map<String, String[]> parameterMap = req.getParameterMap();
        

        String lastTimezone = "";
        int timezoneOffset;

        String timeZone = getAllParametersUrlEncoded(req);

        if (parameterMap.get("timezone") == null) {
            //todo search in Cookie timezone
            Cookie[] cookies = req.getCookies();
            if (cookies != null ) {
                lastTimezone = "timezone = [" + Arrays.stream(cookies)
                        .filter(cookie -> LAST_TIMEZONE.equals(cookie.getName()))
                        .findFirst().get().getValue() + "]";
            }
            System.out.println("lastTimezone: " + lastTimezone);

            timezoneOffset = getTimezoneOffset(lastTimezone);

        } else {

            timezoneOffset = getTimezoneOffset(timeZone);
            System.out.println("timeZone: " + timeZone);
        }



        // We get the current time in LocalDateTime format with UTC time zone
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC).plusHours(timezoneOffset);

        // We form a line with time and time zone
        String sign = timezoneOffset >=0 ? "+":"";
        String time = now
                .format((DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss"))) + " UTC" + sign + timezoneOffset;

        Cookie cookie = new Cookie("lastTimezone","UTC" + sign + timezoneOffset );
        resp.addCookie(cookie);

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("time", time)

        );
        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    private static void setSession(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        session.setAttribute("userGoIT","5671230000000000");
    }

    private int getTimezoneOffset(String timeZone) {
        String timezoneString = "timezone = [2]";
        int timezoneOffset = 0;

        if (timeZone != null) {
            Pattern pattern = Pattern.compile("[-+]?\\d+");
            Matcher matcher = pattern.matcher(timeZone);
            if (matcher.find()) {
                timezoneOffset = Integer.parseInt(matcher.group());
            } else {
                // handle the case where no numeric value was found
            }
        } else {
            timezoneOffset = 0;
        }
        System.out.println("timezoneOffset = " + timezoneOffset);
        return timezoneOffset;
    }

    private String getAllParametersUrlEncoded(HttpServletRequest request){
        //
        StringJoiner result = new StringJoiner("<br>");

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()){
            String parameterName = parameterNames.nextElement();

            String parameterValues = Arrays.toString(request.getParameterValues(parameterName));
            result.add(parameterName + " = " + parameterValues);

        }
        return result.toString();
    }
}
