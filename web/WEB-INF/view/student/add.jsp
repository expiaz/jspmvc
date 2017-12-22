<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="request"/>
<jsp:useBean id="error" class="java.lang.String" scope="request"/>

<h1>Ajouter un étudiant</h1>

<form action="<%= router.build("student.add") %>" method="post">
    <input type="text" name="nom" placeholder="Nom">
    <br/>
    <input type="text" name="prenom" placeholder="Prénom">
    <br/>
    <button>Ajouter</button>
</form>

<%--
<ul>
    <c:forEach items="${students}" var="student">
        <li>${ student.name }</li>
    </c:forEach>
</ul>
--%>