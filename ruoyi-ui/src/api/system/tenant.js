import request from '@/utils/request'

export function listTenant(query) {
  return request({
    url: '/system/tenant/list',
    method: 'get',
    params: query
  })
}

export function getTenant(tenantId) {
  return request({
    url: '/system/tenant/' + tenantId,
    method: 'get'
  })
}

export function addTenant(data) {
  return request({
    url: '/system/tenant',
    method: 'post',
    data: data
  })
}

export function updateTenant(data) {
  return request({
    url: '/system/tenant',
    method: 'put',
    data: data
  })
}

export function renewTenant(data) {
  return request({
    url: '/system/tenant/renew',
    method: 'put',
    data: data
  })
}

export function delTenant(tenantId) {
  return request({
    url: '/system/tenant/' + tenantId,
    method: 'delete'
  })
}

