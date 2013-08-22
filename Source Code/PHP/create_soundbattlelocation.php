<?php
 
/*
 * Following code will create a new profile row
 * All profile details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
require_once '/home/thesis-std/request_key.php';
 
// check for required fields
if (isset($_POST['soundBattleID']) && isset($_POST['latitude1']) && isset($_POST['longitude1']) && isset($_POST['latitude2']) && isset($_POST['longitude2']) && isset($_POST['latitude3']) && isset($_POST['longitude3']) && isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY) {
 
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    $soundBattleID = mysql_real_escape_string($_POST['soundBattleID']);
    $latitude1 = mysql_real_escape_string($_POST['latitude1']);
	$longitude1 = mysql_real_escape_string($_POST['longitude1']);
    $latitude2 = mysql_real_escape_string($_POST['latitude2']);
	$longitude2 = mysql_real_escape_string($_POST['longitude2']);
    $latitude3 = mysql_real_escape_string($_POST['latitude3']);
	$longitude3 = mysql_real_escape_string($_POST['longitude3']);
 
    // mysql inserting a new row

	$result = mysql_query("INSERT INTO soundbattlelocations(soundBattleID,latitude, longitude) VALUES ('$soundBattleID', '$latitude1', '$longitude1'), ('$soundBattleID', '$latitude2', '$longitude2'), ('$soundBattleID', '$latitude3', '$longitude3')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
		$response["soundBattleLocationID"] =  mysql_insert_id();
        $response["message"] = "Sound Battle location with id ".$response["soundBattleLocationID"]." successfully created.";
 
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