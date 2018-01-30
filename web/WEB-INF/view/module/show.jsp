<%@ page import="core.utils.ParameterBag" %>
<%@ page import="entity.Etudiant" %>
<%@ page import="entity.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<jsp:useBean id="module" class="entity.Module" scope="request"/>

<%
    User user = (User) session.getAttribute("user");
%>

<h1>Module ${ module.nom }</h1>

<h4>Etudiants :</h4>
<table>
    <thead>
        <th>Nom</th>
        <th>Prénom</th>
        <th>Moyenne</th>
        <th>Absences</th>
    </thead>
    <tbody>
        <% for (Etudiant etudiant : module.getEtudiants()) { %>
            <tr>
                <td>
                    <a href="<%= router.build("etudiant.show", new ParameterBag().add("etudiant", etudiant.getId()))%>">
                        <strong><%= etudiant.getNom() %></strong>
                    </a>
                </td>
                <td>
                    <%= etudiant.getPrenom() %>
                </td>
                <td>
                    <%= etudiant.getMoyenne(module) %>
                </td>
                <td>
                    <%= etudiant.getAbsences() %>
                </td>
            </tr>
        <% } %>
    </tbody>
</table>

<% if(user.isAdmin()) { %>
    <hr />
    <h4>Modifications :</h4>
    <a href="<%= router.build("module.edit", new ParameterBag().add("module", module.getId())) %>">Editer le module</a>
    <br />
    <a href="<%= router.build("module.note.add", new ParameterBag().add("module", module.getId())) %>">Ajouter une note</a>
<% } %>

