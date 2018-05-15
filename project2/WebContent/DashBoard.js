function handleCardResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);
    debugger;
    console.log("handle card response");
    console.log(resultDataJson);
    console.log(resultDataJson["status2"]);

    // If login success, redirect to index.html page
    if (resultDataJson["status2"] === "success") {
        window.location.replace("confirmation");
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {

        console.log("show error message");
        console.log(resultDataJson["message2"]);
        alert(resultDataJson["message2"]);
        //jQuery("#card_error_message").text(resultDataJson["message2"]);
    }   
}

function submitCardForm(formSubmitEvent) {
    console.log("submit card form");
    debugger;
    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/DashBoard",
        // Serialize the login form to the data sent by POST request
        jQuery("#card_form").serialize(),
        (resultDataString) => handleCardResult(resultDataString));

}
function Result(resultData){
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["table_name"] + "</th>";
        rowHTML += "<th>" + resultData[i]["column_name"] + "</th>";
        rowHTML += "<th>" + resultData[i]["column_type"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

// Bind the submit action of the form to a handler function
jQuery("#card_form").submit((event) => submitCardForm(event));
jQuery.get("api/DashBoard", 
		jQuery("#metadata_table").serialize,
		(resultData) => Result(resultData)); // Setting callback function to handle data returned successfully by the SingleStarServlet

