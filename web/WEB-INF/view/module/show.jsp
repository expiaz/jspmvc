<%@ page import="core.utils.ParameterBag" %>
<%@ page import="entity.Etudiant" %>
<%@ page import="java.util.function.Function" %>
<%@ page import="entity.Note" %>
<%@ page import="java.util.stream.Collector" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<jsp:useBean id="module" class="entity.Module" scope="request"/>

<h1>${ module.nom }</h1>
<ul>
    <%
        for (Etudiant etudiant : module.getEtudiants()) {
            List<String> list = new ArrayList<String>();
            for (Note note : etudiant.getNotes(module)) {
                String s = String.valueOf(note.getValeur());
                list.add(s);
            }
    %>
    <li>
        <a href="<%= router.build("etudiant.show", new ParameterBag().add("etudiant", etudiant.getId()))%>">
            <%= etudiant.getNom() %> <%= etudiant.getPrenom() %>
        </a>
        ( <%= String.join(",", list) %> )
    </li>
    <%
        }
    %>
</ul>
<a href="<%= router.build("module.note.add", new ParameterBag().add("module", module.getId())) %>">Ajouter une note</a>
<br/>
<a href="<%= router.build("module.edit", new ParameterBag().add("module", module.getId())) %>">Edit</a>