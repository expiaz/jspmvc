<%@ page import="core.utils.ParameterBag" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<jsp:useBean id="module" class="entity.Module" scope="request"/>

<h1>Modifier un module</h1>

<form action="<%= router.build("module.edit", new ParameterBag().add("module", module.getId())) %>" method="post">
    <input type="text" name="nom" placeholder="Nom" value="${module.nom}">
    <br/>
    <button>Modifer</button>
</form>

<%--
<ul>
    <c:forEach items="${etudiants}" var="etudiant">
        <li>${ etudiant.name }</li>
    </c:forEach>
</ul>
--%>