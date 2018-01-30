<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="application"/>

<h1>Ajouter un étudiant</h1>

<form action="<%= router.build("etudiant.add") %>" method="post">
    <input type="text" name="nom" placeholder="Nom">
    <br/>
    <input type="text" name="prenom" placeholder="Prénom">
    <br/>
    <input type="email" name="mail" placeholder="Mail">
    <br/>
    <input type="password" name="password" placeholder="Mot de passe">
    <br/>
    <input type="password" name="password2" placeholder="Repêter le mot de passe">
    <br/>
    <button>Ajouter</button>
</form>