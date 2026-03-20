import request from '@/utils/request'

export function listNumber(query) {
  return request({
    url: '/system/number/list',
    method: 'get',
    params: query
  })
}

export function getNumber(numberId) {
  return request({
    url: '/system/number/' + numberId,
    method: 'get'
  })
}

export function addNumber(data) {
  return request({
    url: '/system/number',
    method: 'post',
    data: data
  })
}

export function updateNumber(data) {
  return request({
    url: '/system/number',
    method: 'put',
    data: data
  })
}

export function changeNumberStatus(data) {
  return request({
    url: '/system/number/changeStatus',
    method: 'put',
    data: data
  })
}

export function delNumber(numberId) {
  return request({
    url: '/system/number/' + numberId,
    method: 'delete'
  })
}
