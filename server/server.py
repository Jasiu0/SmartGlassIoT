# -*- coding: utf-8 -*-

import json
import time

from flask import Flask, jsonify, abort, make_response, request

app = Flask(__name__)

default_room = {
    "id": 0,
    "name": "106",
    "description": "Pokój prowadzących",
    "hosts": {
        "Jacek Rumiński": {
            "consultations": "Pon. 9:00-11:00",
            "messages": "Polecam tą aplikację!",
            "present": True,
            "busy": True
        }
    },
    "movement": True,
    "noise": False,
    "light": True,
    "gas": False,
}

qrtokens = {
    "12345": 0
}

rooms = [
    default_room
]

@app.route("/api/rooms", methods=["GET"])
def get_rooms():
    return jsonify({"rooms": rooms})

@app.route("/api/rooms/<int:room_id>", methods=["GET"])
def get_room(room_id):
    room = rooms[room_id]
    if room["id"] != room_id:
        abort(404)

    return jsonify({"room": room})

@app.route("/api/history/<int:room_id>/<int:t_start>:<int:t_end>", methods=["GET"])
def get_room_history(room_id, t_start, t_end):
    try:
        room_history = json.load(open("history.json"))[str(room_id)]
    except:
        abort(404)

    room_history_fragment = {
        "gas": get_sensor_history_fragment(room_history["gas"], t_start, t_end),
        "movement": get_sensor_history_fragment(room_history["movement"], t_start, t_end),
        "light": get_sensor_history_fragment(room_history["light"], t_start, t_end),
        "noise": get_sensor_history_fragment(room_history["noise"], t_start, t_end)
    }

    return jsonify({"roomHistory": room_history_fragment})

@app.route("/api/history/last/<int:room_id>/<string:sensor>")
def get_sensor_last_change(room_id, sensor):
    try:
        room_history = json.load(open("history.json"))[str(room_id)]
        last_sensor_measurement = room_history[sensor][-1]
    except:
        abort(404)

    return jsonify(last_sensor_measurement)


@app.route("/api/qrtokens/<int:qrtoken>", methods=["GET"])
def get_qrtoken(qrtoken):
    qrtoken = str(qrtoken)
    if not qrtoken in qrtokens:
        abort(404)

    return jsonify({"roomId": qrtokens[qrtoken]})

@app.route("/api/rooms", methods=["POST"])
def create_room():
    if not request.json:
        abort(400)

    room = request.json
    room["id"] = len(rooms)
    rooms.append(room)

    return jsonify({"room": room}), 201

@app.route("/api/rooms/<int:room_id>", methods=["POST"])
def modify_room(room_id):
    if not request.json:
        abort(400)

    temp_room = rooms[room_id].copy()
    temp_room.update(request.json)
    if "timestamp" in temp_room:
        del(temp_room["timestamp"])

    if temp_room != rooms[room_id]:
        history = json.load(open("history.json"))

        room_history = history[str(room_id)]

        if temp_room["gas"] != rooms[room_id]["gas"]:
            gas_json = {"timestamp": request.json["timestamp"], "value": request.json["gas"]}
            room_history["gas"].append(gas_json)

        if temp_room["movement"] != rooms[room_id]["movement"]:
            movement_json = {"timestamp": request.json["timestamp"], "value": request.json["movement"]}
            room_history["movement"].append(movement_json)

        if temp_room["light"] != rooms[room_id]["light"]:
            light_json = {"timestamp": request.json["timestamp"], "value": request.json["light"]}
            room_history["light"].append(light_json)

        if temp_room["noise"] != rooms[room_id]["noise"]:
            noise_json = {"timestamp": request.json["timestamp"], "value": request.json["noise"]}
            room_history["noise"].append(noise_json)

        json.dump(history, open("history.json", "w"))

    rooms[room_id] = temp_room

    return jsonify({"room": rooms[room_id]})

@app.route("/api/test", methods=["POST"])
def get_api():
    print "Hello"

    return jsonify({"message": "Hello!"})

@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({"error": "Not found"}), 404)

def get_sensor_history_fragment(history, t_start, t_end):
    history_fragment = []
    for sensor_status in history:
        timestamp = sensor_status["timestamp"]
        if timestamp >= t_start and timestamp <= t_end:
            history_fragment.append(sensor_status)

        if timestamp > t_end:
            break

    return history_fragment


if __name__ == "__main__":
    app.run(host="0.0.0.0")
