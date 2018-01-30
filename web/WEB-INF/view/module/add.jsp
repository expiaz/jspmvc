<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<h1>Ajouter un module</h1>

<form action="<%= router.build("module.add") %>" method="post">
    <input type="text" name="nom" placeholder="Nom">
    <br/>
    <button>Ajouter</button>
</form>