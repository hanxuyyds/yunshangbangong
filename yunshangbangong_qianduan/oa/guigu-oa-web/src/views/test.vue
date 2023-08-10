<template>
  <div>
    <div>账号切换</div>
    <button @click="zhangsan()" type="default" size="mini">张三</button>
    <button @click="lisi()" type="default" size="mini">李四</button>
    <button @click="zzjl()" type="default" size="mini">张总经理</button>
    <button @click="admin()" type="default" size="mini">admin</button>
    <div>当前token：{{ token }}</div>
  </div>
</template>

<script>
export default {
  name: "Test",

  data() {
    return {
      token: ''
    };
  },

  created(){
    this.token = window.localStorage.getItem('token')
  },

  methods: {
    admin() {
      window.localStorage.setItem('token', '');
      let token ='eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLM0sjQzNzMz0FEqLU4t8kwBikGYeYm5qUAtiSm5mXlKtQD3z4XRQgAAAA.cHmUhhBG2mnPOWCXM8HStAwTJS4ZYNJX_c8JWQLpFYj51PdrD6HzfPKY6VbsboT6h86efXhND8Zc8OnXrtZIxQ'

      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },
    zhangsan() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLM0sjQztzQ11VEqLU4t8kwBihlC2HmJualAPVUZiXnpxYl5SrUA7XV0P0YAAAA.EpVxUxItXgB9DaJakieuTLmBLJrrj-UwUl7Jyw88H40Zcnifd-6l1JDm_cV_5SbQ6su0LwYvRxOgek2sy1S70A'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },


    zzjl() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDM3NjMwNLYw11EqLU4t8kxRsjKFMPMSc1OBWqqqsnKUagGHebUNQQAAAA.FcCkD6QfaP8bd_3MYsRD6Tp9wr9JSRFWIUuIdG5O7XxhIeyWfzGLp40_Lesx_kWMMaaskh_ba3NrTlj8-nP6OA'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    lisi() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLM0sjS3MDS31FEqLU4t8kxRsjKDMPMSc1OBWnIyizOVagFSHftEQQAAAA.TwaRSBnilIsQgrRminzNOt0yQle0JEgQhYhraKrUA0meeDIKb6cgEbKEubd3hec3WLKEGHrtDLOc7XzM7nXMiw'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    }
  }
}
</script>
