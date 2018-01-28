<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>

<ul>
    <li>
        <a href="<%= router.build("etudiant.list") %>">Voir les étudiants</a>
    </li>
</ul>