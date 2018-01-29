<%@ page import="core.utils.ParameterBag" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="application"/>

<jsp:useBean id="note" class="entity.Note" scope="request"/>

<h1>Edition de la note du module ${ note.module.nom } pour ${ note.etudiant.nom } ${ note.etudiant.prenom }</h1>

<form action="<%= router.build("note.edit", new ParameterBag().add("note", note.getId())) %>" method="post">
    <label>
        Note : <input type="text" name="note" value="<%= note.getValeur() %>"/>
    </label>
    <br/>
    <button>Editer</button>
</form>
