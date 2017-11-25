<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="factory.GestionFactory" %>
<%@ page import="entity.Etudiant" %>

<ul>
    <% for (Etudiant etudiant : GestionFactory.getEtudiants()) { %>
    <li>
        <a href="show.jsp?etudiant=<%= etudiant.getId() %>">
            <%= etudiant.getNom() %> - <%= etudiant.getPrenom() %>
        </a>
    </li>
    <% } %>
</ul>