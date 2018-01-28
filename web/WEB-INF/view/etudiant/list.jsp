<%@ page import="java.util.Collection" %>
<%@ page import="entity.Etudiant" %>
<%@ page import="core.utils.ParameterBag" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<h1>Etudiants</h1>
<ul>
    <%
        for (Etudiant etudiant: (Collection<Etudiant>) request.getAttribute("etudiants")) {
    %>
        <li>
            <a href="<%= router.build("etudiant.show", new ParameterBag().add("etudiant", etudiant.getId())) %>">
                <strong><%= etudiant.getNom() %></strong> <%= etudiant.getPrenom() %>
            </a>
        </li>
    <%
        }
    %>
</ul>

<%--
<ul>
    <c:forEach items="${etudiants}" var="etudiant">
        <li>${ etudiant.name }</li>
    </c:forEach>
</ul>
--%>