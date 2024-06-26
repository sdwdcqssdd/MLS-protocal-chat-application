import {connectServer} from "@/scripts/chat/chatSocket";
import {ref} from "vue";
import {cachedRequest} from "@/scripts/cachedRequest";
import {getUser} from "@/scripts/user";
import {apiBaseAddress} from "@/scripts/api";
import axios from "axios";

const chatApi = apiBaseAddress + 'chat/'

const newChatApi = chatApi + 'new-chat'

const addNewChat = (memberIds) => {
    return axios.post(
        newChatApi,
        memberIds,
        {
            headers: {
                'User-Id': localStorage.getItem('user_id'),
                'User-Token': localStorage.getItem('user_token')
            },
        }
    )
}

const cachedGetUserName = cachedRequest(async (userId) => {
    let result = await getUser(userId)
    return result.data.data.name
})

function getActivatedChat(client) {
    if (client === undefined || client.activatedChatId === undefined) {
        return undefined
    }
    for (let idx in client.chats) {
        const chat = client.chats[idx]
        if (chat.id === client.activatedChatId) {
            return chat
        }
    }
    return undefined
}

function getChats(client) {
    return client.chats
}

function getMessages(client) {
    return client.displayedMessages
}

function changeChat(client, chatId) {
    client.activatedChatId = chatId
    updateDisplayedMessages({value: client})
    updateChats({value: client})
}

function updateDisplayedMessages(client) {
    let displayed = []
    let newestTime = 0
    for (let idx in client.value.messages) {
        const row = client.value.messages[idx]
        if (row.chatId === client.value.activatedChatId) {
            displayed.push(row)
            newestTime = Math.max(newestTime, row.time)
        }
    }
    let chat = client.value.chats.find(e => e.id === client.value.activatedChatId)
    if (chat.readUntil < newestTime) {
        chat.readUntil = newestTime
        client.value.socket.readMessage(chat.id, newestTime)
    }
    displayed.sort((lhs, rhs) => lhs.time - rhs.time)
    client.value.displayedMessages = displayed
    client.value.notifyNewMessage()
}

function makeUpdateChats(client) {
    return function (chat) {
        chat.memberNames = []
        for (let idx in chat.memberIds) {
            let memberId = chat.memberIds[idx]
            if (memberId === parseInt(localStorage.getItem('user_id'))) {
                continue
            }
            client.value.getUserName(memberId).then((name) => {
                chat.memberNames.push(name)
                if (chat.memberNames.length === chat.memberIds.length - 1) {
                    chat.memberNames.sort()
                    chat.name = chat.memberNames.join()
                    client.value.chats.push(chat)
                }
                updateChats(client)
            })
        }
    }
}

function makeUpdateMessages(client, notifyNewMessage) {
    return function (message) {
        client.value.getUserName(message.senderId).then((name) => {
            message.senderName = name
            client.value.messages.push(message)
            if (message.chatId === client.value.activatedChatId) {
                updateDisplayedMessages(client)
            }
            updateChats(client)
        })
    }
}

function updateChats(client) {
    let idToChat = new Map()
    let newestMessage = new Map()
    for (let idx in client.value.chats) {
        let chat = client.value.chats[idx]
        idToChat.set(chat.id, chat)
        chat.unreadCount = 0
    }
    for (let idx in client.value.messages) {
        let message = client.value.messages[idx]
        if (newestMessage.has(message.chatId)) {
            if (newestMessage.get(message.chatId).time < message.time) {
                newestMessage.set(message.chatId, message)
            }
        } else {
            newestMessage.set(message.chatId, message)
        }
        if (idToChat.has(message.chatId)) {
            let chat = idToChat.get(message.chatId)
            if (chat.readUntil < message.time) {
                chat.unreadCount += 1
            }
        }
    }
    for (let idx in client.value.chats) {
        let chat = client.value.chats[idx]
        if (!newestMessage.has(chat.id)) {
            chat.newestMessage = undefined
            continue
        }
        chat.newestMessage = newestMessage.get(chat.id)
    }
    client.value.chats.sort((lhs, rhs) => {
        let lTime = (lhs.newestMessage === undefined ? 0 : lhs.newestMessage.time)
        let rTime = (rhs.newestMessage === undefined ? 0 : rhs.newestMessage.time)
        if (lTime !== rTime) {
            return rTime - lTime
        }
        return lhs.id - rhs.id
    })
}

function sendMessage(client, content) {
    if (client.activatedChatId === undefined) {
        return
    }
    client.socket.sendMessage(client.activatedChatId, content)
}

function initChat(notifyNewMessage) {
    let client = ref({
        chats: [],
        displayedMessages: [],
        messages: [],
        activatedChatId: undefined,
        getUserName: cachedGetUserName,
        socket: {},
        notifyNewMessage: notifyNewMessage
    });
    client.value.socket = connectServer(
        makeUpdateChats(client),
        makeUpdateMessages(client),
        () => 0
    )

    return client
}

export {
    initChat,
    getChats,
    getMessages,
    getActivatedChat,
    changeChat,
    sendMessage,
    addNewChat,
}