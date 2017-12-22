<%@ page import="core.utils.ParameterBag" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="request"/>
<jsp:useBean id="error" class="java.lang.String" scope="request"/>

<jsp:useBean id="student" class="entity.Etudiant" scope="request"/>

<h1>Modifier un étudiant</h1>

<form action="<%= router.build("student.edit", new ParameterBag().add("student", student.getId())) %>" method="post">
    <input type="text" name="nom" placeholder="Nom" value="${student.nom}">
    <br/>
    <input type="text" name="prenom" placeholder="Prénom" value="${student.prenom}">
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