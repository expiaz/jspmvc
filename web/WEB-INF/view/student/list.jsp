<%@ page import="java.util.Collection" %>
<%@ page import="entity.Etudiant" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="request"/>

<h1>Etudiants</h1>
<ul>
    <%
        for (Etudiant student: (Collection<Etudiant>) request.getAttribute("students")) {
    %>
        <li>
            <a href="<%= router.build("student.show", new String[][] {
                    new String[]{"student", student.getId().toString()}
            }) %>">
                <strong><%= student.getNom() %></strong> <%= student.getPrenom() %>
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