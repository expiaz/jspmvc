<%@ page import="entity.Module" %>
<%@ page import="java.util.Set" %>
<%@ page import="core.utils.ParameterBag" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<jsp:useBean id="etudiant" class="entity.Etudiant" scope="request"/>

<h1>Ajouter une note à <%= etudiant.getNom() %></h1>

<form action="<%= router.build("etudiant.note.add", new ParameterBag().add("etudiant", etudiant.getId())) %>" method="post">
    <select name="module">
        <%
            for(Module module : etudiant.getModules()) {
        %>
        <option value="<%= module.getId() %>">
            <%= module.getNom() %>
        </option>
        <%
            }
        %>
    </select>
    <input name="note" type="text" required placeholder="14.5"/>
    <br/>
    <button>Ajouter</button>
</form>