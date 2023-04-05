package servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

//        String timezone = "UTC+3";
        String timezone = req.getParameter("timezone");

        if (!isValidTimezone(timezone)){
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid timezone");//
            return;
        }
        chain.doFilter(req,res);
    }

    private boolean isValidTimezone(String timezone) {
        try{
            int offset = getTimezoneOffset(timezone);
            return offset >= -12 && offset <=12;
        }catch (IllegalArgumentException e) {
            return false;
        }
    }

    private int getTimezoneOffset(String timezone) {

        int timezoneOffset = 0;
        if (timezone != null) {
            Pattern pattern = Pattern.compile("[-+]?\\d+");
            Matcher matcher = pattern.matcher(timezone);
            if (matcher.find()) {
                timezoneOffset = Integer.parseInt(matcher.group());
            } else {
                // handle the case where no numeric value was found
            }
        }else {
            timezoneOffset = 0;
        }
        System.out.println("timezoneOffset = " + timezoneOffset);
        return timezoneOffset;
    }
}
