<%@ page import="entity.Module" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Set" %>
<%@ page import="core.utils.ParameterBag" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<jsp:useBean id="etudiant" class="entity.Etudiant" scope="request"/>

<h1>Ajouter un module à <%= etudiant.getNom() %></h1>

<%
    Set<Module> modules = etudiant.getModules();
%>

<form action="<%= router.build("etudiant.module.add", new ParameterBag().add("etudiant", etudiant.getId())) %>" method="post">
    <select name="module">
        <%
            for(Module module : (Collection<Module>) request.getAttribute("modules")) {
        %>
        <option
                value="<%= module.getId() %>"
                <%
                    if(modules.contains(module)) {
                %>
                    disabled
                <%
                    }
                %>
        >
            <%= module.getNom() %>
        </option>
        <%
            }
        %>
    </select>
    <br/>
    <button>Ajouter</button>
</form>