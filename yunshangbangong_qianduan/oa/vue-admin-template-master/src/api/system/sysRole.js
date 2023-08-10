import request from '@/utils/request'
const api_name='/admin/system/sysRole'
export default{
    //角色列表-条件分页查询
    getPageList(current,limit,searchObj){
        return request({
            url:`${api_name}/${current}/${limit}`,
            method:'get',
            params:searchObj
        })
    },
    //角色删除
    removeById(id){
        return request({
            url:`${api_name}/remove/${id}`,
            method:'delete',
        })
    },
    //添加角色
    saveRole(role){
        return request({
            url:`${api_name}/save`,
            method:'get',
            params:role
        })
    },
    //根据id查询
    getById(id){
        return request({
            url:`${api_name}/get/${id}`,
            method:'get'
        })
    },
    //修改角色
    updateById(role){
        return request({
            url:`${api_name}/update`,
            method:'get',
            params:role
        })
    },
    //批量删除
    batchRemove(idList){
        return request({
            url:`${api_name}/batchRemove`,
            method:'delete',
            data:idList
        })
    },
    getRoles(adminId) {
        return request({
          url: `${api_name}/toAssign/${adminId}`,
          method: 'get'
        })
      },
      
      assignRoles(assginRoleVo) {
        return request({
          url: `${api_name}/doAssign`,
          method: 'post',
          data: assginRoleVo
        })
      }

}