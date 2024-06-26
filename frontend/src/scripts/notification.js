import {ElNotification} from "element-plus";

const makeNotification = (ok, message) => {
    if (ok) {
        ElNotification({
            title: 'Success',
            message: message,
            type: 'success',
        })
    } else {
        ElNotification({
            title: 'Error',
            message: message,
            type: 'error',
        })
    }
}

export {
    makeNotification,
}