<?php
define('API_URL', 'http://localhost:8081/api');
define('ADMIN_USER', 'admin');
define('ADMIN_PASS', 'admin123');

function callAPI($endpoint, $postData = null) {
    $url = API_URL . $endpoint;
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
    if ($postData) {
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($postData));
    }
    $result = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);
    if ($httpCode == 200) {
        return json_decode($result, true);
    }
    return null;
}

function getStats() {
    return callAPI('/stats');
}

function getUsers() {
    $data = callAPI('/users');
    return $data ? $data['users'] : [];
}

function getItems() {
    $data = callAPI('/items');
    return $data ? $data['items'] : [];
}

function getRooms() {
    $data = callAPI('/rooms');
    return $data ? $data['rooms'] : [];
}

function getLogs() {
    $data = callAPI('/logs');
    return $data ? $data['logs'] : [];
}

function adminAction($action, $userId = null) {
    return callAPI('/admin', [
        'action' => $action,
        'username' => ADMIN_USER,
        'password' => ADMIN_PASS
    ]);
}

function userAction($action, $userId) {
    return callAPI('/users', [
        'action' => $action,
        'userId' => $userId
    ]);
}

function itemAction($action, $data) {
    $payload = array_merge(['action' => $action], $data);
    return callAPI('/items', $payload);
}

function roomAction($action, $id) {
    return callAPI('/rooms', [
        'action' => $action,
        'id' => $id
    ]);
}
