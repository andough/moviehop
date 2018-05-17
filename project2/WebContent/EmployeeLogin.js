/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);
    // If login success, redirect to index.html page
    if (resultDataJson["status"] === "success") {
        window.location.replace("DashBoard.html");
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {

        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitForm(formSubmitEvent) {
    console.log("submit login form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/EmployeeLogin",
        // Serialize the login form to the data sent by POST request
        jQuery("#employee_form").serialize(),
        (resultDataString) => handleResult(resultDataString));

}

// Bind the submit action of the form to a handler function
jQuery("#employee_form").submit((event) => submitForm(event));

