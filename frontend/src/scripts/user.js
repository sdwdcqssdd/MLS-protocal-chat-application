import {apiBaseAddress} from './api'
import axios from "axios";
import {router} from "@/router";

const userApi = apiBaseAddress + 'user/'

const getUserApi = userApi + 'get'
const userLoginApi = userApi + 'login'
const checkUsernameApi = userApi + 'check-username'
const userRegisterApi = userApi + 'register'
const changeUsernameApi = userApi + 'change-username'
const changePasswordApi = userApi + 'change-password'
const userAvatarApi = userApi + 'avatar'
const changeAvatarApi = userApi + 'change-avatar'
const searchUserApi = userApi + 'search'
const listUsersApi = userApi + 'list'

const getUserType = () => {
    if (localStorage.getItem('is_admin') !== null) {
        return 'admin'
    } else if (localStorage.getItem('username') !== null) {
        return 'user'
    }
    return undefined
}

const getUser = (id) => {
    return axios.get(
        getUserApi,
        {
            params: {
                id: id
            }
        }
    )
}

const userLogin = (username, password) => {
    return axios.post(
        userLoginApi,
        {
            name: username,
            password: password
        }
    )
}

const checkUsernameAvailable = (username) => {
    return axios.post(
        checkUsernameApi,
        {
            name: username
        }
    )
}

const userRegister = (username, password) => {
    return axios.post(
        userRegisterApi,
        {
            name: username,
            password: password
        }
    )
}

const changeUsername = (username) => {
    return axios.post(
        changeUsernameApi,
        {
            newName: username,
        },
        {
            headers: {
                'User-Id': localStorage.getItem('user_id'),
                'User-Token': localStorage.getItem('user_token')
            },
        }
    )
}

const changePassword = (password) => {
    return axios.post(
        changePasswordApi,
        {
            newPassword: password,
        },
        {
            headers: {
                'User-Id': localStorage.getItem('user_id'),
                'User-Token': localStorage.getItem('user_token')
            },
        }
    )
}

const getAvatarURL = (id = undefined) => {
    if (id === undefined) {
        id = parseInt(localStorage.getItem('user_id'))
    }
    return userAvatarApi + '?id=' + id.toString()
}

const changeAvatar = (avatar) => {
    let formData = new FormData()
    formData.append('avatar', avatar)
    return axios.post(
        changeAvatarApi,
        formData,
        {
            headers: {
                'User-Id': localStorage.getItem('user_id'),
                'User-Token': localStorage.getItem('user_token'),
                'Content-Type': 'multipart/form-data',
            },
        }
    )
}

const searchUser = (pattern) => {
    return axios.get(
        searchUserApi,
        {
            params: {
                pattern: pattern
            },
            headers: {
                'User-Id': localStorage.getItem('user_id'),
                'User-Token': localStorage.getItem('user_token'),
            }
        }
    )
}

const listUser = (isAdmin) => {
    return axios.get(
        listUsersApi,
        {
            params: {
                admin: isAdmin
            },
            headers: {
                'User-Id': localStorage.getItem('user_id'),
                'User-Token': localStorage.getItem('user_token'),
            }
        }
    )
}

const ensureLogin = () => {
    if (localStorage.getItem('user_token') == null) {
        router.push('/login')
    }
}

export {
    getUser,
    getUserType,
    userLogin,
    checkUsernameAvailable,
    userRegister,
    changeUsername,
    changePassword,
    getAvatarURL,
    changeAvatar,
    searchUser,
    listUser,
    ensureLogin,
}