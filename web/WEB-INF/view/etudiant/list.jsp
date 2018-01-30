<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="java.util.Collection" %>
<%@ page import="entity.Etudiant" %>
<%@ page import="core.utils.ParameterBag" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<h1>Etudiants</h1>
<table>
    <thead>
        <th><strong>Nom</strong> & prénom</th>
        <th>Absences</th>
        <th>Moyenne générale</th>
    </thead>
    <tbody>
        <% for (Etudiant etudiant: (Collection<Etudiant>) request.getAttribute("etudiants")) { %>
            <tr>
                <td>
                    <a href="<%= router.build("etudiant.show", new ParameterBag().add("etudiant", etudiant.getId())) %>">
                        <strong><%= etudiant.getNom() %></strong> <%= etudiant.getPrenom() %>
                    </a>
                </td>
                <td>
                    <%= etudiant.getAbsences() %>
                </td>
                <td>
                    <%= etudiant.getMoyenne() %>
                </td>
            </tr>
        <% } %>
    </tbody>
</table>

<%--
<ul>
    <c:forEach items="${etudiants}" var="etudiant">
        <li>${ etudiant.name }</li>
    </c:forEach>
</ul>
--%>