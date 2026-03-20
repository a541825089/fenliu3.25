import request from '@/utils/request'

// 查询链接列表
export function listLink(query) {
  return request({
    url: '/system/link/list',
    method: 'get',
    params: query
  })
}

// 查询链接详细
export function getLink(linkId) {
  return request({
    url: '/system/link/' + linkId,
    method: 'get'
  })
}

// 新增链接
export function addLink(data) {
  return request({
    url: '/system/link',
    method: 'post',
    data: data
  })
}

// 修改链接
export function updateLink(data) {
  return request({
    url: '/system/link',
    method: 'put',
    data: data
  })
}

// 删除链接
export function delLink(linkId) {
  return request({
    url: '/system/link/' + linkId,
    method: 'delete'
  })
}

// 导出链接
export function exportLink(query) {
  return request({
    url: '/system/link/export',
    method: 'get',
    params: query
  })
}
