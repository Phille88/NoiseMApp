<?php

// response json
$json = array();

/**
 * Registering a user device
 * Store reg id in users table
 */
if (isset($_POST["userID"]) && isset($_POST["email"]) && isset($_POST["regId"])) {
    $userID = $_POST["userID"];
    $email = $_POST["email"];
    $gcm_regid = $_POST["regId"]; // GCM Registration ID
    // Store user details in db
    include_once __DIR__ . '/db_functions.php';
    include_once __DIR__ . '/GCM.php';

    $db = new DB_Functions();
    $gcm = new GCM();

    $res = $db->storeUser($userID, $email, $gcm_regid);

    $registatoin_ids = array($gcm_regid);
    $message = array("price" => "Congratulations! You installed NoiseMApp!");

    $result = $gcm->send_notification($registatoin_ids, $message);

    echo $result;
} else {
    // user details missing
}
?>