<?php
 
/*
 * Following code will delete a product from table
 * A product is identified by product id (pid)
 */
require_once '/home/thesis-std/request_key.php';
 
// array for JSON response
$response = array();

$tables=array();
$tables["badges"] = array("userID","badgeID");
$tables["friendships"] = array("friendshipID","userID1","userID2");
$tables["gcm_users"] = array("id","gcm_regid","userID","email","created_at");
$tables["noisehuntrecordings"] = array("userID","noiseHuntID","noiseRecordingID","lastUpdated");
$tables["noisehuntstate"] = array("userID","noiseHuntID", "lastUpdated");
$tables["noiserecordings"] = array("noiseID","userID","latitude","longitude","dB","accuracy","quality","timeStamp");
$tables["soundbattlelocations"] = array("soundBattleLocationID","soundBattleID","longitude","latitude");
$tables["soundbattlerecordings"] = array("userID","soundBattleLocationID","noiseRecordingID","lastUpdated");
$tables["soundbattles"] = array("soundBattleID","userID1","userID2", "createdAt");
$tables["soundbattletotalpoints"] = array("soundBattleID","pointsUserID1","pointsUserID2", "createdAt");
$tables["soundcheckinrecordings"] = array("userID","placeName","noiseRecordingID", "createdAt");
$tables["totalpoints"] = array("userID","totalPoints");
$tables["userprofiles"] = array("userID","googleID_1","googleID_2","firstName","lastName","email","pictureURL","createdAt","updatedAt");

// check for required fields
if (isset($_POST['requestKey']) && $_POST['requestKey'] == REQUESTKEY) {
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
    
	foreach($tables as $tableName => $columns){
		$result = mysql_query("SELECT * FROM $tableName");
		// check if row deleted or not
		if (mysql_num_rows($result) > 0) {
			$response[$tableName] = array();
			while($row = mysql_fetch_array($result)){
				$columnValues = array();
				foreach($columns as $column){
					$columnValues[$column] = mysql_real_escape_string($row[$column]);
				}
				unset($column);
				array_push($response[$tableName],$columnValues);
			}
		} else {
			$response[$tableName]["message"] = "Nothing found in table $tableName.";
		}
	}
	echo json_encode($response);
	unset($tableName);
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Nothing has changed...";
 
    // echoing JSON response
    echo json_encode($response);
}
?>