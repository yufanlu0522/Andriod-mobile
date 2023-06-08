<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype") %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <p>Currency Conversion</p>
        <form action="getCurrencyscoop" method="GET">
<%--            <label for="letter">From:</label>--%>
<%--            <input type="text" name="currencyCodeFrom" value="" /><br>--%>
<%--            <label for="letter">To:</label>--%>
<%--            <input type="text" name="currencyCodeTo" value="" /><br>--%>
<%--            <label for="letter">Amount:</label>--%>
<%--            <input type="text" name="currencyAmount" value="" /><br>--%>
            <input type="submit" value="Click To View Dashboard" />
        </form>
    </body>
</html>

