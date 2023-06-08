<%@ page import="java.util.ArrayList" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
    <head>
        <title>Currencyscoop</title>
        <style>
            table,
            th,
            td {
                padding: 10px;
                border: 1px solid black;
                border-collapse: collapse;
            }
        </style>
    </head>
    <body>
        <h1>Currency Conversion</h1>
        <table>
            <caption>All Request/Reply Logs</caption>
            <tr>
                <th>Request Time</th>
                <th>Reply Time</th>
                <th>From Currency</th>
                <th>To Currency</th>
                <th>Amount</th>
                <th>Status Code</th>
                <th>Conversion Rate</th>
            </tr>
            <%
                ArrayList<String> logList = (ArrayList<String>) request.getAttribute("logString");
                String logString = logList.get(0);
                String[] logs = logString.split("\n");
                for(String log : logs)
                {
                    String[] attr = log.split(",");
            %>
            <tr>
                <td><%= attr[0] %></td>
                <td><%= attr[1] %></td>
                <td><%= attr[2] %></td>
                <td><%= attr[3] %></td>
                <td><%= attr[4] %></td>
                <td><%= attr[5] %></td>
                <td><%= attr[6] %></td>
            </tr>
            <%}%>
        </table>
        <h1>Operation analytics</h1>
        <table>
            <caption>Top 5 Countries Converting Currency from</caption>
            <tr>
                <th></th>
                <th>Top1</th>
                <th>Top2</th>
                <th>Top3</th>
                <th>Top4</th>
                <th>Top5</th>
            </tr>
            <%
                String fromString = logList.get(1);
                String[] attr = fromString.split(",");
            %>
            <tr>
                <th>Currency Code</th>
                <td><%= attr[0] %></td>
                <td><%= attr[2] %></td>
                <td><%= attr[4] %></td>
                <td><%= attr[6] %></td>
                <td><%= attr[8] %></td>
            </tr>
            <tr>
                <th>Count</th>
                <td><%= attr[1] %></td>
                <td><%= attr[3] %></td>
                <td><%= attr[5] %></td>
                <td><%= attr[7] %></td>
                <td><%= attr[9] %></td>
            </tr>
        </table>
        <table>
            <caption>Top 5 Countries Converting Currency to</caption>
            <tr>
                <th></th>
                <th>Top1</th>
                <th>Top2</th>
                <th>Top3</th>
                <th>Top4</th>
                <th>Top5</th>
            </tr>
            <%
                String toString = logList.get(2);
                attr = toString.split(",");
            %>
            <tr>
                <th>Currency Code</th>
                <td><%= attr[0] %></td>
                <td><%= attr[2] %></td>
                <td><%= attr[4] %></td>
                <td><%= attr[6] %></td>
                <td><%= attr[8] %></td>
            </tr>
            <tr>
                <th>Count</th>
                <td><%= attr[1] %></td>
                <td><%= attr[3] %></td>
                <td><%= attr[5] %></td>
                <td><%= attr[7] %></td>
                <td><%= attr[9] %></td>
            </tr>
        </table>
        <table>
            <caption>Average Latency Per Request</caption>
            <%
                String latencyString = logList.get(3);
            %>
            <tr>
                <th>Average Latency Per Request</th>
                <td><%= latencyString + " milliseconds" %></td>
            </tr>
        </table>
    </body>
</html>

