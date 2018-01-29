<%@ page import="java.util.Collection" %>
<%@ page import="core.utils.ParameterBag" %>
<%@ page import="entity.Module" %>
<%@ page import="entity.Etudiant" %>
<%@ page import="java.util.Set" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<h1>Modules</h1>
<table>
    <thead>
        <tr>
            <th>Nom</th>
            <th>Etudiants</th>
        </tr>
    </thead>
    <tbody>
        <% for (Module module : (Collection<Module>) request.getAttribute("modules")) { %>
            <tr>
                <td>
                    <a href="<%= router.build("module.show", new ParameterBag().add("module", module.getId())) %>">
                        <strong><%= module.getNom() %></strong>
                    </a>
                </td>
                <td>
                    <%= module.getEtudiants().size() %>
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