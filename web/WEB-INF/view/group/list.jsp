<%@ page import="java.util.Collection" %>
<%@ page import="core.utils.ParameterBag" %>
<%@ page import="entity.Groupe" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="request"/>

<h1>Groupes</h1>
<ul>
    <%
        for (Groupe groupe: (Collection<Groupe>) request.getAttribute("groups")) {
    %>
        <li>
            <a href="<%= router.build("group.show", new ParameterBag().add("group", groupe.getId())) %>">
                <strong><%= groupe.getNom() %></strong>
            </a>
        </li>
    <%
        }
    %>
</ul>

<%--
<ul>
    <c:forEach items="${students}" var="student">
        <li>${ student.name }</li>
    </c:forEach>
</ul>
--%>