<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>

<form action="<%= router.build("index.login") %>" method="POST">
    <label>
        Login :
        <input name="login" type="text" required placeholder="john@doe.com"/>
    </label>
    <br/>
    <label>
        Mot de passe:
        <input name="password" type="password" min="8" required />
    </label>
    <br/>
    <button>Connexion</button>
</form>