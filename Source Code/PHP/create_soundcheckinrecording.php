<?php
 
/*
 * Following code will create a new profile row
 * All profile details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
require_once '/home/thesis-std/request_key.php';
 
// check for required fields
if (isset($_POST['userID']) && isset($_POST['placeName']) && isset($_POST['noiseRecordingID']) && isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY){
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    $userID = mysql_real_escape_string($_POST['userID']);
    $placeName = mysql_real_escape_string($_POST['placeName']);
	$noiseRecordingID = mysql_real_escape_string($_POST['noiseRecordingID']);
 
    // mysql inserting a new row

	$result = mysql_query("INSERT INTO soundcheckinrecordings (userID,placeName,noiseRecordingID) VALUES ('$userID', '$placeName', '$noiseRecordingID')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Sound Checkin recording successfully created.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
        echo json_encode($response);
    }
} else {
	// required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>