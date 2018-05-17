function handleCardResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);
    debugger;
    console.log("handle card response");
    console.log(resultDataJson);
    console.log(resultDataJson["status2"]);
    // If login success, redirect to index.html page
    if (resultDataJson["status2"] === "success") {
    	alert("success");
    	 window.location.replace("single-movie.html?id=" + resultDataJson["movieid"]);
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
        console.log("show error message");
        console.log(resultDataJson["message2"]);
        //alert("error");
        alert(resultDataJson["message2"]);
        //jQuery("#card_error_message").text(resultDataJson["message2"]);
    }   
}

function handleStarResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);
    debugger;
    console.log("handle card response");
    console.log(resultDataJson);
    console.log(resultDataJson["status2"]);
    // If login success, redirect to index.html page
    if (resultDataJson["status2"] === "success") {
    	alert("success");
    	window.location.replace("single-star.html?id=" + resultDataJson["starid"]);
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
        console.log("show error message");
        console.log(resultDataJson["message2"]);
        alert(resultDataJson["message2"]);
        //alert("error");
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
function submitStarForm(formSubmitEvent) {
    console.log("submit card form");
    //alert(formSubmitEvent);
    debugger;
    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/InsertStar",
        // Serialize the login form to the data sent by POST request
        jQuery("#star_form").serialize(),
        (resultDataString) => handleStarResult(resultDataString));

}
function handleResult(resultData){
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
jQuery("#star_form").submit((event) => submitStarForm(event));
$.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/MetaData", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: function (resultData) { // Setting callback function to handle data returned successfully by the StarsServlet
    	handleResult(resultData);
    }, 
    error: function (resultData) {
        debugger;
        console.log("there was an error");
    },
    complete: function (resultData) {
        debugger;
        console.log("End Of Ajax call!");
        //A function to be called when the request finishes 
        // (after success and error callbacks are executed). 
    }
});

