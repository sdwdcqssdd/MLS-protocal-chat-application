const socketBase = 'ws://localhost:9090/socket/'
const chatSocketURL = socketBase + 'chat'

const MessageType = {
    INIT: 0,
    NEW_CHAT: 1,
    NEW_MESSAGE: 2,
    READ_MESSAGE: 3,
}

function sendMessage(socket, type, data) {
    const json = JSON.stringify({
        type: type,
        data: data
    })
    socket.send(json)
}

function makeInitializer(socket, fail) {
    return function (event) {
        sendMessage(socket, MessageType.INIT, JSON.stringify({
            userId: localStorage.getItem('user_id'),
            userToken: localStorage.getItem('user_token')
        }))
    }
}

function makeDispatcher(newChat, newMessage, fail) {
    return function (event) {
        const response = JSON.parse(event.data)
        if (response.type === MessageType.NEW_CHAT) {
            newChat(JSON.parse(response.data))
        } else if (response.type === MessageType.NEW_MESSAGE) {
            newMessage(JSON.parse(response.data))
        } else {
            fail()
        }
    }
}

function makeSendMessage(socket) {
    return function (chatId, message) {
        sendMessage(socket, MessageType.NEW_MESSAGE, JSON.stringify({
            chatId: chatId,
            content: message,
            senderId: parseInt(localStorage.getItem('user_id'))
        }))
    }
}

function makeReadMessage(socket) {
    return function (chatId, readUntil) {
        sendMessage(socket, MessageType.READ_MESSAGE, JSON.stringify({
            chatId: chatId,
            readUntil: readUntil
        }))
    }
}

function connectServer(newChat, newMessage, fail) {
    const socket = new WebSocket(chatSocketURL)
    socket.addEventListener("open", makeInitializer(socket, fail))
    socket.addEventListener("message", makeDispatcher(newChat, newMessage, fail))
    return {
        sendMessage: makeSendMessage(socket),
        readMessage: makeReadMessage(socket),
    }
}

export {
    connectServer,
}