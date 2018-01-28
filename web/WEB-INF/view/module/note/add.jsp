<%@ page import="core.utils.ParameterBag" %>
<%@ page import="entity.Etudiant" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<jsp:useBean id="module" class="entity.Module" scope="request"/>

<h1>Ajouter une note aux étudiants du module <%= module.getNom() %></h1>

<form action="<%= router.build("module.note.add", new ParameterBag().add("module", module.getId())) %>" method="post">
    <% for (Etudiant etudiant : module.getEtudiants()) { %>
        <label>
            <strong><%= etudiant.getNom() %></strong> <%= etudiant.getPrenom() %>
            <input type="text" name="notes[<%= etudiant.getId() %>]" placeholder="12.5" required/>
        </label>
        <br/>
    <% } %>
    <button>Ajouter</button>
</form>