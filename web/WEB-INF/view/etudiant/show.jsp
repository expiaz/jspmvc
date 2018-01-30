<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="core.utils.ParameterBag" %>
<%@ page import="entity.Module" %>
<%@ page import="entity.Note" %>
<%@ page import="entity.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<jsp:useBean id="etudiant" class="entity.Etudiant" scope="request"/>

<%
    User user = (User) session.getAttribute("user");
%>

<h1><strong>${ etudiant.nom }</strong> ${ etudiant.prenom }</h1>

<div>
    <% if(user.isAdmin() && etudiant.getAbsences() > 0) { %>
    <form class="inline" action="<%= router.build("etudiant.absence.edit", new ParameterBag().add("etudiant", etudiant.getId()).add("number", -1)) %>"
          method="post">
        <button>-</button>
    </form>
    <% } %>
    <p class="inline">
        ${ etudiant.absences } absences
    </p>
    <% if(user.isAdmin()) { %>
    <form class="inline" action="<%= router.build("etudiant.absence.edit", new ParameterBag().add("etudiant", etudiant.getId()).add("number", 1)) %>"
          method="post">
        <button>+</button>
    </form>
    <% } %>
</div>

<p>Moyenne générale : <%= etudiant.getMoyenne() %></p>

<hr />
<h4>Modules :</h4>
<table>
    <thead>
        <tr>
            <th>Nom</th>
            <th>Notes</th>
            <th>Moyenne</th>
        </tr>
    </thead>
    <tbody>
        <% for(Module module : etudiant.getModules()) { %>
        <tr>
            <td>
                <a href="<%= router.build("module.show", new ParameterBag().add("module", module.getId())) %>">
                    <%= module.getNom() %>
                </a>
            </td>
            <td>
                <% if(etudiant.getNotes(module).size() > 0) { %>
                    <ul class="no-margin">
                        <% for(Note note : etudiant.getNotes(module)) { %>
                        <li>
                            <% if(user.isAdmin()) { %>
                                <a href="<%= router.build("note.edit", new ParameterBag().add("note", note.getId())) %>">
                                    <%= note.getValeur() %> / 20
                                </a>
                            <% } else { %>
                                <%= note.getValeur() %> / 20
                            <% } %>
                        </li>
                        <% } %>
                    </ul>
                <% } else { %>
                    Aucune note
                <% } %>
            </td>
            <td>
                <%= etudiant.getMoyenne(module) %>
            </td>
        </tr>
        <% } %>
    </tbody>
</table>

<% if(user.isAdmin()) { %>
    <hr />
    <h4>Modifications : </h4>
    <a href="<%= router.build("etudiant.edit", new ParameterBag().add("etudiant", etudiant.getId())) %>">Modifier le profil</a>
    <br/>
    <a href="<%= router.build("etudiant.module.add", new ParameterBag().add("etudiant", etudiant.getId())) %>">Affecter un module</a>
    <% if(!etudiant.getModules().isEmpty()) { %>
        <br/>
        <a href="<%= router.build("etudiant.note.add", new ParameterBag().add("etudiant", etudiant.getId())) %>">Ajouter une note</a>
    <% } %>
<% } %>