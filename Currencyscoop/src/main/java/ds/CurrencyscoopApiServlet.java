package ds;
/*
 * @author Yufan Lu, Yuting Long
 * AndrewID: yufanlu, yutinglo
 *
 * this is the servlet that provides api call funtion to android
 * it will return data in Json form
 */

import com.google.gson.Gson;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/*
 * The following WebServlet  annotation specifies the URL ending with "/getApiCurrencyscoop"
 *
 */
@WebServlet(name = "ApiCurrencyscoopServlet",
        urlPatterns = {"/getApiCurrencyscoop"})
public class CurrencyscoopApiServlet extends HttpServlet {
    public class CurrencyResponse {

        private String from;

        private String to;

        private String amount;

        private String result;

        // constructors

        public CurrencyResponse(String from, String to, String amount, String result) {
            this.from = from;
            this.to = to;
            this.amount = amount;
            this.result = result;
        }
        // standard getters and setters.
    }
    private Gson gson = new Gson();

    CurrencyscoopModel ipm = null;  // The "business model" for this app

    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        ipm = new CurrencyscoopModel();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        // get the search parameter if it exists
        String from = request.getParameter("currencyCodeFrom");
        String to = request.getParameter("currencyCodeTo");
        String amount = request.getParameter("currencyAmount");

        String responseString = ipm.getCurrencyList(from, to, amount);
        ResponseMessage responseMessage = gson.fromJson(responseString, ResponseMessage.class);
        String value = String.valueOf(responseMessage.response.value);


        // getting the response by calling the 3rd-party API
        CurrencyResponse r = new CurrencyResponse(from, to, amount, value);
        String currencyJsonString = this.gson.toJson(r);

        // printing the response
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(currencyJsonString);
        out.flush();
    }
}

