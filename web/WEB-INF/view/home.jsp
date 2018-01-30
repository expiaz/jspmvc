<%@ page import="entity.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>

<h1>Application de gestion</h1>
<h4>Pour les absences et notes des étudiants en JEE</h4>

<%
    User user = (User) session.getAttribute("user");
%>

<% if(user == null) { %>
    <p>Pour profiter pleinement de cette application veuillez <a href="<%= router.build("index.login") %>">vous connecter</a></p>
<% } %>