<%@ page import="core.utils.ParameterBag" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="request"/>

<jsp:useBean id="group" class="entity.Groupe" scope="request"/>

<h1>Modifier un groupe</h1>

<form action="<%= router.build("group.edit", new ParameterBag().add("group", group.getId())) %>" method="post">
    <input type="text" name="nom" placeholder="Nom" value="${group.nom}">
    <br/>
    <button>Modifer</button>
</form>

<%--
<ul>
    <c:forEach items="${students}" var="student">
        <li>${ student.name }</li>
    </c:forEach>
</ul>
--%>