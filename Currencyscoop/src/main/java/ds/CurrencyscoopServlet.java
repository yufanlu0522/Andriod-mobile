package ds;
/*
 * @author Yufan Lu, Yuting Long
 * AndrewID: yufanlu, yutinglo
 *
 * this is the servlet the provides the dashboard function
 */

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

/*
 * The following WebServlet annotation specify the URL ending with "/getCurrencyscoop"
 */
@WebServlet(name = "CurrencyscoopServlet",
        urlPatterns = {"/getCurrencyscoop"})
public class CurrencyscoopServlet extends HttpServlet {

    CurrencyscoopModel ipm = null;  // The "business model" for this app

    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        ipm = new CurrencyscoopModel();
    }

    // This servlet will reply to HTTP GET requests via this doGet method
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // get the search parameter if it exists
        String from = request.getParameter("currencyCodeFrom");
        String to = request.getParameter("currencyCodeTo");
        String amount = request.getParameter("currencyAmount");

        // determine what type of device our user is
        String ua = request.getHeader("User-Agent");

        // prepare the appropriate DOCTYPE for the view pages

        String nextView;
        // use model to do the search and choose the result view
        List<String> list = ipm.getLogList();

        for(int i =0 ; i < list.size(); i++){
            System.out.println(list.get(i));
        }

        /*
         * Attributes on the request object can be used to pass data to
         * the view.  These attributes are name/value pairs, where the name
         * is a String object.  Here the pictureURL is passed to the view
         * after it is returned from the model interestingPictureSize method.
         */
        request.setAttribute("logString", list);
        // Pass the user search string (pictureTag) also to the view.
        nextView = "result.jsp";
        // Transfer control over the the correct "view"
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }
}

