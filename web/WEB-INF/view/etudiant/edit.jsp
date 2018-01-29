<%@ page import="core.utils.ParameterBag" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<jsp:useBean id="etudiant" class="entity.Etudiant" scope="request"/>

<h1>Modifier le profil de <strong>${ etudiant.nom }</strong> ${ etudiant.prenom }</h1>

<form action="<%= router.build("etudiant.edit", new ParameterBag().add("etudiant", etudiant.getId())) %>" method="post">
    <input type="text" name="nom" placeholder="Nom" value="${etudiant.nom}">
    <br/>
    <input type="text" name="prenom" placeholder="Prénom" value="${etudiant.prenom}">
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