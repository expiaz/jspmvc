<%@ page import="core.utils.ParameterBag" %>
<%@ page import="entity.Module" %>
<%@ page import="entity.Note" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<jsp:useBean id="etudiant" class="entity.Etudiant" scope="request"/>

<h1>${ etudiant.nom }</h1>
<h2>${ etudiant.prenom }</h2>
<p>${ etudiant.absences }</p>

<ul>
    <%
        for(Module module : etudiant.getModules()) {
    %>
        <li>
            <a href="<%= router.build("module.show", new ParameterBag().add("module", module.getId()))%>">
                <%= module.getNom() %>
            </a>
        </li>
    <%
        }
    %>
</ul>

<ul>
    <%
        for(Note note : etudiant.getNotes()) {
    %>
    <li>
        <a href="<%= router.build("module.show", new ParameterBag().add("module", note.getModule().getId()))%>">
            <%= note.getValeur() %>
        </a>
    </li>
    <%
        }
    %>
</ul>

<a href="<%= router.build("etudiant.edit", new ParameterBag().add("etudiant", etudiant.getId())) %>">Edit</a>