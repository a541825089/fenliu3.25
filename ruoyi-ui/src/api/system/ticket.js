import request from '@/utils/request'

export function listTicket(query) {
  return request({
    url: '/system/ticket/list',
    method: 'get',
    params: query
  })
}

export function getTicket(ticketId) {
  return request({
    url: '/system/ticket/' + ticketId,
    method: 'get'
  })
}

export function addTicket(data) {
  return request({
    url: '/system/ticket',
    method: 'post',
    data: data
  })
}

export function updateTicket(data) {
  return request({
    url: '/system/ticket',
    method: 'put',
    data: data
  })
}

export function delTicket(ticketId) {
  return request({
    url: '/system/ticket/' + ticketId,
    method: 'delete'
  })
}
